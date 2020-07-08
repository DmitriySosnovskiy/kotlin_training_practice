package presenters

sealed class Event {
    object OnStartAlgorithm : Event()
    object AfterAlgorithmStarted : Event()

    class SaveGraph(val fileName: String): Event()
    class DownloadGraph (val fileName: String): Event()
    object Clear : Event()
    object PreviousStep : Event()
    object NextStep : Event()
    object GenerateGraph : Event()
    object EndAlgorithm : Event()

    class LogEvent(val logInfo: String) : Event()
}