package com.fatmasatyani.githubuser2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fatmasatyani.githubuser2.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val listGithub = MutableLiveData<ArrayList<Github>>()
    private lateinit var adapter: GithubAdapter
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = GithubAdapter (arrayListOf()) {
            val moveIntent = Intent (this@MainActivity,DetailActivity::class.java)
            (moveIntent.putExtra(DetailActivity.EXTRA_GITHUB,it))
            startActivity(moveIntent) }
        adapter.notifyDataSetChanged()

        binding.rvGithub.layoutManager = LinearLayoutManager(this)
        binding.rvGithub.adapter = adapter

        binding.btnSearch.setOnClickListener{
            getUser(binding.edtText.text.toString())

        }
    }

    private fun getUser (username: String?) {
        binding.progressBar.visibility = View.VISIBLE
        val listItems = ArrayList<Github>()
        val user = AsyncHttpClient()
        val url = "https://api.github.com/search/users?q="+ binding.edtText.getText()
        user.addHeader("Authorization", "token d482eebf2803d1bcac229b1a250222c5b58f961d")
        user.addHeader("User-Agent", "request")
        user.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>, responseBody: ByteArray) {
                binding.progressBar.visibility = View.INVISIBLE

                try {
                    val result = String (responseBody)
                    val responseObject = JSONObject(result)
                    val list = responseObject.getJSONArray("items")
                    for (i in 0 until list.length()) {
                        val githubItems = list.getJSONObject(i)
                        val username = githubItems.getString("login")
                        val avatar = githubItems.getString("avatar_url")
                        val user = Github()
                        user.username = username
                        user.avatar = avatar
                        listItems.add(user)
                    }
                    adapter.setData(listItems)
                    showLoading(false)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
                Log.d(TAG, "Sukses ${responseBody.toString()} $statusCode")
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>, responseBody: ByteArray,error: Throwable) {
                binding.progressBar.visibility = View.INVISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Log.d(TAG, "Gagal $error ${responseBody.toString()} $statusCode")
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun getGithub(): LiveData<ArrayList<Github>> {
        return listGithub
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}