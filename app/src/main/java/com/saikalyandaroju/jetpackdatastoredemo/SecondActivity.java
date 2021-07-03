package com.saikalyandaroju.jetpackdatastoredemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.core.Serializer;
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException;
import androidx.datastore.rxjava2.RxDataStore;
import androidx.datastore.rxjava2.RxDataStoreBuilder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

public class SecondActivity extends AppCompatActivity {
    RxDataStore<User> dataStore;

    EditText name,age;
    TextView value;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        name=findViewById(R.id.name);
        age=findViewById(R.id.age);
        value=findViewById(R.id.preference_value);
        save=findViewById(R.id.ok);
        initPrefs();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uName=name.getText().toString();
                int uAge=Integer.valueOf(age.getText().toString());
                if(!uName.isEmpty() && uAge>=0){
                    saveData(uName,uAge);
                }
            }
        });

        readData();

    }

    private void readData() {

        dataStore.data().map(new Function<User, Users>() {
            @Override
            public Users apply(@NonNull User user) throws Exception {
                return (user==null)?new Users("name",0):new Users(user.getName(),user.getAge());
            }
        }).toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Users>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Users users) {
              value.setText(users.getAge()+" is the age of  "+users.getName());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }



    private void saveData(String uName, int uAge) {
        dataStore.updateDataAsync(new Function<User, Single<User>>() {
            @Override
            public Single<User> apply(@NonNull User user) throws Exception {
                  //user.toBuilder().clear() clearing prefernce
                return Single.just(user.toBuilder().setAge(uAge).setName(uName).build()) ;
            }
        });

    }

    private void initPrefs() {

        dataStore =
                new RxDataStoreBuilder<User>(SecondActivity.this, "users.pb", new SettingsSerializer()).build();
    }

    private static class SettingsSerializer implements Serializer<User> {

        @Override
        public User getDefaultValue() {
            return User.getDefaultInstance();
        }

        @Nullable
        @Override
        public Object readFrom(@NotNull InputStream inputStream, @NotNull Continuation<? super User> continuation) {
            try {
                return User.parseFrom(inputStream);
            } catch (Exception e) {

            }
            return null;

        }

        @Nullable
        @Override
        public Object writeTo(User user, @NotNull OutputStream outputStream, @NotNull Continuation<? super Unit> continuation) {
            try {
                user.writeTo(outputStream);
                return user;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}