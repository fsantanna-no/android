package org.freechains.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PeersFragment : Fragment ()
{
    val outer = this
    lateinit var main: MainActivity

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        main = this.activity as MainActivity
        inflater.inflate(R.layout.frag_peers, container, false).let { view ->
            view.findViewById<ExpandableListView>(R.id.list).let {
                it.setAdapter(this.adapter)
                it.setOnItemLongClickListener { _,view,_,_ ->
                    if (view is LinearLayout && view.tag is String) {
                        this.main.peers_remove_ask(view.tag.toString()) {
                            this.adapter.notifyDataSetChanged()
                        }
                        true
                    } else {
                        false
                    }
                }
            }
            view.findViewById<FloatingActionButton>(R.id.but_add).let {
                it.setOnClickListener {
                    this.main.peers_add_ask {
                        this.adapter.notifyDataSetChanged()
                    }
                }
            }
            return view
        }
    }

    private val adapter = object : BaseExpandableListAdapter() {
        override fun hasStableIds(): Boolean {
            return false
        }
        override fun isChildSelectable (i: Int, j: Int): Boolean {
            return true
        }
        override fun getChild (i: Int, j: Int): Any? {
            return LOCAL!!.peers[i].chains[j]
        }
        override fun getChildId (i: Int, j: Int): Long {
            return i*10+j.toLong()
        }
        override fun getChildView (i: Int, j: Int, isLast: Boolean,
                                   convertView: View?, parent: ViewGroup?): View? {
            val view = View.inflate(outer.main, R.layout.frag_peers_chain,null)
            val chain = LOCAL!!.peers[i].chains[j].toString()
            view.findViewById<TextView>(R.id.chain).text = chain.chain2id()
            if (!LOCAL!!.chains.any { it.name == LOCAL!!.peers[i].chains[j] }) {
                view.findViewById<ImageButton>(R.id.add).let {
                    it.visibility = View.VISIBLE
                    it.setOnClickListener {
                        outer.main.chains_join(chain) {
                            println("added: $chain")
                            this.notifyDataSetChanged()
                        }
                    }

                }
            }
            return view
        }
        override fun getChildrenCount (i: Int): Int {
            return LOCAL!!.peers[i].chains.size
        }
        override fun getGroupCount(): Int {
            return LOCAL!!.peers.size
        }
        override fun getGroup (i: Int): Any {
            return LOCAL!!.peers[i]
        }
        override fun getGroupId (i: Int): Long {
            return i.toLong()
        }
        override fun getGroupView (i: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
            val view = View.inflate(outer.main, R.layout.frag_peers_host,null)
            view.findViewById<TextView>(R.id.ping).text = LOCAL!!.peers[i].ping
            view.findViewById<TextView>(R.id.host).text = LOCAL!!.peers[i].name
            view.tag = LOCAL!!.peers[i].name
            return view
        }
    }
}