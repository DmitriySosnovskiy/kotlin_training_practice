package presenters

import javax.swing.JFileChooser

interface ToolbarView {
    fun lockSaveAndDownload()
    fun lockStart()
}

class ToolbarPresenter(toolbarView: ToolbarView) {
    fun handleEvent(event: Event) {
        //add when
        BroadcastPresenter.generateEvent(event)
    }

    fun getFilePath() : String? {
        val fileChooser = JFileChooser()
        val action = fileChooser.showDialog(null, "Выберите файл")

        if (action == JFileChooser.APPROVE_OPTION)
        {
            val file = fileChooser.selectedFile

            return file.absolutePath
        }

        return null
    }
}