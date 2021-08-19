package kh.com.bongyab.rxandroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {
    private val TAG: String = "RxAndroidSample"
    private val disposable: CompositeDisposable = CompositeDisposable()
    private var resultView: TextView? = null
    private var numberCalled: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "MainActivity")

        resultView = findViewById(R.id.tvResult)

        findViewById<Button>(R.id.button_run_scheduler).setOnClickListener {
            onRunSchedulerExampleButtonClicked()
        }
    }

    private fun onRunSchedulerExampleButtonClicked() {
        val observable = sampleObservable()
                        // Run on a background thread
                        .subscribeOn(Schedulers.io())
                        // Be notified on the main thread
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<String?>() {
                            override fun onComplete() {
                                Log.d(TAG, "onComplete($numberCalled)")
                                numberCalled ++
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError()", e)
                            }

                            @SuppressLint("SetTextI18n")
                            override fun onNext(t: String?) {
                                Log.d(TAG, "onNext($t)")
                                val viewText: String = resultView!!.text.toString()
                                resultView!!.text = "$viewText, \nHello, $t, $numberCalled Called"
                            }
                        })

        disposable.add(observable)
    }

    private fun sampleObservable(): Observable<String> {
        return Observable.defer { // Do some long running operation
            SystemClock.sleep(5000)
            Observable.just("Cambodia Art", "Cambodia Boxing", "Cambodia Music", "Cambodia Festival", "Cambodia Award")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}