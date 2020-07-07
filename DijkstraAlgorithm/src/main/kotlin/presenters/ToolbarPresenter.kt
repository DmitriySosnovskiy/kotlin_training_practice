package presenters

import javax.swing.JFileChooser

enum class ToolbarViewElement {
    START,
    SAVE,
    DOWNLOAD,
    CLEAR,
    NEXT_STEP,
    PREVIOUS_STEP,
    GENERATE
}

interface ToolbarView {
    fun lockElement(element: ToolbarViewElement)
    fun unlockElement(element: ToolbarViewElement)
    fun getFilePath() : String?
}

class ToolbarPresenter(private val toolbarView: ToolbarView) : EventSubscriber {

    fun chainToolbarEvent(event: Event) = BroadcastPresenter.generateEvent(event)

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.AfterAlgorithmStarted -> {
                toolbarView.unlockElement(ToolbarViewElement.NEXT_STEP)
                toolbarView.unlockElement(ToolbarViewElement.PREVIOUS_STEP)
            }
        }
    }

    fun getFilePath() = toolbarView.getFilePath()
}