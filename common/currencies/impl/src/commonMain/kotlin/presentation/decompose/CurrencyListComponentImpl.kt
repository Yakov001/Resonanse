package presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import data.repository.CurrenciesRepository
import data.Response
import data.repository.CurrenciesRepositoryNew
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.decompose.CurrencyListScreenState.LoadingStatus
import domain.model.CurrencyEntity
import kotlinx.coroutines.flow.Flow
import utils.SnackbarAction
import utils.SnackbarController
import utils.SnackbarEvent

@OptIn(FlowPreview::class)
class CurrencyListComponentImpl(
    componentContext: ComponentContext,
    private val onCurrencySelected: (CurrencyEntity) -> Unit,
    private val onBackClick: () -> Unit,
    private val componentScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) : CurrencyListComponent, ComponentContext by componentContext, KoinComponent {

    private val repo: CurrenciesRepository by inject()
    private val newRepo: CurrenciesRepositoryNew by inject()

    private val _screenState = MutableStateFlow(CurrencyListScreenState())
    override val screenState: StateFlow<CurrencyListScreenState> = _screenState
        .onStart { fetchCurrencies() }
        .stateIn(
            scope = componentScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CurrencyListScreenState()
        )

    init {
        lifecycle.doOnDestroy { componentScope.coroutineContext.cancelChildren() }

        CoroutineScope(Dispatchers.Default).launch {
            _screenState
                .distinctUntilChanged { old, new -> old.searchText == new.searchText }
                .debounce(500)
                .collectLatest {
                    sortCurrenciesByName()
                }
        }
    }

    override fun searchByName(searchText: String) {
        _screenState.update { it.copy(searchText = searchText) }
    }

    override fun refreshCurrencies() {
        _screenState.update { it.copy(loadingStatus = LoadingStatus.Loading) }
        componentScope.launch {
            repo.updateCurrenciesFromRemote()
                .onCompletion { _screenState.update { it.copy(loadingStatus = LoadingStatus.Idle) } }
                .handleResponse()
        }
    }

    private fun fetchCurrencies() {
        componentScope.launch {
            val response = newRepo.getCurrencies()
            response.handleResponse()

//            repo.getCurrencies()
//                .onCompletion { _screenState.update { it.copy(loadingStatus = LoadingStatus.Idle) } }
//                .handleResponse()
        }
    }

    override fun onCurrencyClick(currency: CurrencyEntity) = onCurrencySelected.invoke(currency)

    override fun onBackClick() = onBackClick.invoke()

    private fun sortCurrenciesByName() {
        _screenState.update {
            it.copy(sortedData = it.data.sortedBySearchText())
        }
    }

    private suspend fun Response<List<CurrencyEntity>>.handleResponse() {
        when (this) {
            is Response.Success -> {
                _screenState.update {
                    it.copy(
                        data = this.data,
                        sortedData = this.data
                    )
                }
                sortCurrenciesByName()
            }

            is Response.Loading -> {
                _screenState.update { it.copy(loadingStatus = LoadingStatus.Loading) }
            }

            is Response.Failure -> {
                _screenState.update { it.copy(loadingStatus = LoadingStatus.Idle) }
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = this.message,
                        action = SnackbarAction(
                            name = "Retry",
                            action = {
                                fetchCurrencies()
                            }
                        )
                    )
                )
            }
        }
    }

    private suspend fun Flow<Response<List<CurrencyEntity>>>.handleResponse() {
        collectLatest { response ->
            when (response) {
                is Response.Success -> {
                    _screenState.update {
                        it.copy(
                            data = response.data,
                            sortedData = response.data
                        )
                    }
                    sortCurrenciesByName()
                }

                is Response.Loading -> {
                    _screenState.update { it.copy(loadingStatus = LoadingStatus.Loading) }
                }

                is Response.Failure -> {
                    _screenState.update { it.copy(loadingStatus = LoadingStatus.Idle) }
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = response.message,
                            action = SnackbarAction(
                                name = "Retry",
                                action = {
                                    fetchCurrencies()
                                }
                            )
                        )
                    )
                }
            }
        }
    }
}