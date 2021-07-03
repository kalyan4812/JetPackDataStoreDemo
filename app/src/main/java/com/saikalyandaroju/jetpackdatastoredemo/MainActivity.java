package com.saikalyandaroju.jetpackdatastoredemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    TextInputEditText editText;
    TextView value;
    Button savedata;

    RxDataStore<Preferences> dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editext);
        value = findViewById(R.id.textView2);
        savedata = findViewById(R.id.button2);

        initPrefs();

        savedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty()) {
                    save("my_key", editText.getText().toString());

                }
            }
        });
        read("my_key");

    }

    public void save(String key, String value) {
        dataStore.updateDataAsync(new Function<Preferences, Single<Preferences>>() {
            @Override
            public Single<Preferences> apply(@NonNull Preferences preferences) throws Exception {
                MutablePreferences mutablePreferences = preferences.toMutablePreferences();

                Preferences.Key<String> EXAMPLE_KEY = PreferencesKeys.stringKey(key);

                if (EXAMPLE_KEY == null) {
                    Log.i("check", "Key is null");
                } else {
                    Log.i("check", "Key is not null"+EXAMPLE_KEY);
                }
               /* deletion
               if (preferences.contains(EXAMPLE_KEY)) {
                    mutablePreferences.remove(EXAMPLE_KEY);
                }*/


                mutablePreferences.set(EXAMPLE_KEY, (EXAMPLE_KEY == null) ? "" : editText.getText().toString());

                return Single.just(mutablePreferences);
            }
        });
    }

    public void read(String key) {
        Preferences.Key<String> EXAMPLE_KEY = PreferencesKeys.stringKey(key);
        if (EXAMPLE_KEY == null) {
            Log.i("check", "Key is null");
        } else {
            Log.i("check", "Key is not null"+EXAMPLE_KEY);
        }
        dataStore.data().map(new Function<Preferences, String>() {
            @Override
            public String apply(@NonNull Preferences preferences) throws Exception {
                return preferences.get(EXAMPLE_KEY) == null ? "" : preferences.get(EXAMPLE_KEY);
            }
        }).toObservable().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String s) {
                 value.setText(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        //In general create separate util class for datstore and pass as constructor parameter in reository.

        /*subscribe(new FlowableSubscriber<String>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {
               s.request(1);
            }

            @Override
            public void onNext(String s) {
                Log.i("check",s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        value.setText(s);
                    }
                });

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });*/


    }

    private void initPrefs() {
        dataStore =
                new RxPreferenceDataStoreBuilder(this, "settings").build();
    }

    public void next(View view) {
        startActivity(new Intent(this,SecondActivity.class));
    }
}