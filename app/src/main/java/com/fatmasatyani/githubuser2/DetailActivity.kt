package com.fatmasatyani.githubuser2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.fatmasatyani.githubuser2.databinding.ActivityDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val TAG = "DetailActivity"

    companion object {
        const val EXTRA_GITHUB = "extra_github"

        @StringRes
        private val TAB_TITLES = intArrayOf (
                R.string.tab_text_1,
                R.string.tab_text_2
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataGithub: Github? = intent.getParcelableExtra(EXTRA_GITHUB)
        val username = "${dataGithub?.username}"
        getUser(username)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, username)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
            viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tablayout)
            TabLayoutMediator(tabs,viewPager) {tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        supportActionBar?.elevation = 0f
    }

    private fun getUser (username: String?) {
        binding.progressBar.visibility = View.VISIBLE
        val listItems = ArrayList<Github>()
        val user = AsyncHttpClient()
        val url = "https://api.github.com/users/" + username
        user.addHeader("Authorization", "token d482eebf2803d1bcac229b1a250222c5b58f961d")
        user.addHeader("User-Agent", "request")
        user.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>, responseBody: ByteArray) {
                binding.progressBar.visibility = View.INVISIBLE
                try {
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val name = responseObject.getString("name")
                    val username = responseObject.getString("login")
                    val location = responseObject.getString("location")
                    val company = responseObject.getString("company")
                    val repository = responseObject.getInt("public_repos")
                    val follower = responseObject.getInt("followers")
                    val following = responseObject.getInt("following")
                    val avatar = responseObject.getString("avatar_url")
                    val user = Github()
                    user.name = name
                    user.username = username
                    user.location = location
                    user.company = company
                    user.repository = repository
                    user.followers = follower
                    user.following = following
                    user.avatar = avatar

                    binding.apply {
                        Glide.with(this@DetailActivity)
                                .load(user.avatar)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .apply(RequestOptions().override(460, 460))
                                .into(imgUserPhoto)
                        txtUserName.text = "${user.name}"
                        txtUserId.text = "${user.username}"
                        txtUserLocation.text = "${user.location}"
                        txtUserCompany.text = "${user.company}"
                        txtUserRepository.text = "${user.repository} Repositories"
                        txtUserFollower.text = "${user.followers} Followers"
                        txtUserFollowing.text = "${user.following} Following"

                        Log.d(TAG, "${user.company}")

                        showLoading(false)
                    }
                } catch (e: Exception) {
                        Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}




