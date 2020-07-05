package presenters

interface PresenterObserverViewInterface{
    fun updateModel()
}
interface PresenterObserverModelInterface{
    fun updateView()
}

class MainPresenter(
    val graphView: GraphView
) : PresenterObserverViewInterface,PresenterObserverModelInterface{
    override fun updateModel() {
        TODO("Not yet implemented")
    }

    override fun updateView() {
        TODO("Not yet implemented")
    }
}