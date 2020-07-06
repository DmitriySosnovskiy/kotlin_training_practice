package presenters

interface ToolbarView {
    fun lockSaveAndDownload()
    fun lockStart()
}

class ToolbarPresenter(toolbarView: ToolbarView) {
    fun handleEvent(event: Event) {

    }
}