package `in`.co.localnetworklogs

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LocalNetworkLogsActivity : AppCompatActivity() {

    lateinit var adapter: LogsAdapter
    var arrLogs = ArrayList<ModelLog>()

    var foundAtArray = ArrayList<Int>()

    lateinit var rcLogs: RecyclerView
    lateinit var imgClear: ImageView
    lateinit var et_search: EditText
    lateinit var imgMenu: ImageView
    lateinit var txtNoLogs: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        rcLogs = findViewById(R.id.rcLogs)
        imgClear = findViewById(R.id.imgClear)
        et_search = findViewById(R.id.et_search)
        imgMenu = findViewById(R.id.imgMenu)
        txtNoLogs = findViewById(R.id.txtNoLogs)

        var highlighter = TextHighlighter()
            .setBackgroundColor(Color.parseColor("#FFFF00"))
            .setForegroundColor(Color.BLACK)
            .setBold(true)
            .setItalic(true)

        rcLogs.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        adapter = LogsAdapter(arrLogs, highlighter)
        rcLogs.adapter = adapter

        loadLogs()

        imgClear.setOnClickListener {
            et_search.setText("")
        }

        imgMenu.setOnClickListener {
            showMenu()
        }

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(str: CharSequence, p1: Int, p2: Int, p3: Int) {

                foundAtArray.clear()

                if (str.isNotEmpty()) {
                    adapter.textToHightlight = str.toString().lowercase()
                    for (i in arrLogs.indices) {
                        if (arrLogs[i].logstring.lowercase()
                                .contains(str.toString().lowercase())
                        ) {
                            foundAtArray.add(i)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    if (foundAtArray.size > 0) {
                        rcLogs.scrollToPosition(foundAtArray[0])
                    }
                    imgClear.visibility = View.VISIBLE
                } else {
                    adapter.textToHightlight = ""
                    adapter.notifyDataSetChanged()
                    imgClear.visibility = View.GONE
                }
            }

        })
    }

    fun showMenu() {
        val popup = PopupMenu(this@LocalNetworkLogsActivity, imgClear)
        popup.menu.add(1, 1, 1, "Clear Logs")
        popup.show()

        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == 1) {
                LocalNetworkLogsManager.getInstance().clearLogs()
                loadLogs()
            }

            true
        }
    }

    fun loadLogs() {

        if (LocalNetworkLogsManager.getInstance().getString("logs") != null) {
            arrLogs.clear()
            val lines = LocalNetworkLogsManager.getInstance().getString("logs")
                .split(System.getProperty("line.separator")!!)
            for (i in lines.indices) {
                when {
                    isJSONValid(lines[i]) -> {
                        var modellog = ModelLog(formatString(lines[i]), "json")
                        arrLogs.add(modellog)
                    }

                    lines[i].contains("http") -> {
                        var modellog = ModelLog(formatString(lines[i]), "http")
                        arrLogs.add(modellog)
                    }
                    else -> {
                        var modellog = ModelLog(formatString(lines[i]))
                        arrLogs.add(modellog)
                    }
                }
            }

            adapter.notifyDataSetChanged()

            if (LocalNetworkLogsManager.getInstance().getString("logs") == "") {
                txtNoLogs.visibility = View.VISIBLE
            } else {
                txtNoLogs.visibility = View.GONE
            }

        }

    }

    fun isJSONValid(test: String): Boolean {
        try {
            JSONObject(test)
        } catch (ex: JSONException) {
            try {
                JSONArray(test)
            } catch (ex1: JSONException) {
                return false
            }

        }
        return true
    }

    fun formatString(text: String): String {

        val json = StringBuilder()
        var indentString = ""

        for (i in 0 until text.length) {
            val letter = text[i]
            when (letter) {
                '{', '[' -> {
                    json.append("\n" + indentString + letter + "\n")
                    indentString += "\t"
                    json.append(indentString)
                }
                '}', ']' -> {
                    indentString = indentString.replaceFirst("\t".toRegex(), "")
                    json.append("\n" + indentString + letter)
                }
                ',' -> json.append(letter + "\n" + indentString)

                else -> json.append(letter)
            }
        }

        return json.toString()
    }

    class ModelLog(
        var logstring: String = "",
        var logtype: String = ""
    )

    class LogsAdapter(private var listLogs: ArrayList<ModelLog>, var highlighter: TextHighlighter) :
        RecyclerView.Adapter<LogsAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            bindViews(holder, position)
        }

        private fun bindViews(holder: MyViewHolder, position: Int) {

            if (textToHightlight == "") {
                holder.txt_log.text = listLogs[position].logstring
            } else {
                holder.txt_log.text = highlighter.highlightString(
                    listLogs[position].logstring,
                    textToHightlight,
                    TextHighlighter.BASE_MATCHER
                )
            }

            if (listLogs[position].logtype == "http") {
                holder.txt_log.setTextColor(Color.parseColor("#3B14BE"))
            } else if (listLogs[position].logtype == "json") {
                holder.txt_log.setTextColor(Color.parseColor("#8C0032"))
            } else {
                holder.txt_log.setTextColor(Color.parseColor("#000000"))
            }
        }

        override fun getItemCount(): Int {
            return listLogs.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val txt_log: TextView = itemView.findViewById(R.id.txt_log) as TextView
        }

        var textToHightlight = ""

        fun setTextToHighLight(textToHightlight: String) {
            this.textToHightlight = textToHightlight
        }
    }
}
