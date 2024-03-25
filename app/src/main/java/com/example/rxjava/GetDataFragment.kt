package com.example.rxjava

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rxjava.MainActivity.Companion.DATA_AVAILABLE
import com.example.rxjava.MainActivity.Companion.DATA_OVER
import com.example.rxjava.databinding.FragmentGetDataBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import java.io.InputStream

class GetDataFragment : Fragment() {

    private lateinit var binding: FragmentGetDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGetDataBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {

            btnGetDataJSON.setOnClickListener {

                val dispose = dataSourceJson()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            tvJsonDataState.text = DATA_AVAILABLE
                            tvJsonData.text = it
                            Log.d(TAG, "Json data: $it")
                        },
                        {

                        },
                        {
                            tvJsonDataState.text = DATA_OVER
                        }
                    )
            }
        }
    }

    fun dataSourceJson(): Observable<String> {
        var json: String?
        var jsonArray: JSONArray

        return Observable.create { subscriber ->
            val inputStream: InputStream? = activity?.assets?.open("lessonsData.json")
            json = inputStream?.bufferedReader().use { it?.readText() }
            jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                subscriber.onNext(jsonArray.getString(i))
                Thread.sleep(500L)
            }
            subscriber.onComplete()
        }

    }
}