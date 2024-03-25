package com.example.rxjava

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rxjava.MainActivity.Companion.DATA_AVAILABLE
import com.example.rxjava.MainActivity.Companion.DATA_OVER
import com.example.rxjava.databinding.FragmentShowDataBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL

const val URL = "https://jsonplaceholder.typicode.com/posts"

class ShowDataFragment : Fragment() {

    private lateinit var binding: FragmentShowDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowDataBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {

            btnGetDataREST.setOnClickListener {

                val dispose = dataSourceRest()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            tvRestDataState.text = DATA_AVAILABLE
                            tvRestData.text = it
                            Log.d(TAG, "Rest data: $it")
                        },
                        {

                        },
                        {
                            tvRestDataState.text = DATA_OVER
                        }
                    )

            }

        }

    }

    private fun dataSourceRest(): Observable<String> {
        return Observable.create { subscriber ->
            val data = BufferedReader(
                InputStreamReader(
                    URL(URL).openConnection().getInputStream()
                )
            )
            var dataItem: String? = data.readLine()

            while (dataItem != null) {
                subscriber.onNext(dataItem)
                dataItem = data.readLine()
                Thread.sleep(500L)
            }

            subscriber.onComplete()
        }
    }

}