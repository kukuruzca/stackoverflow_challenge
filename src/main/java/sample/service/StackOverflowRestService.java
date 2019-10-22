package sample.service;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sample.service.responses.CommonWrapperObject;
import sample.service.responses.StackOverflowUser;

import java.awt.*;
import java.io.IOException;


public final class StackOverflowRestService {
    public static final String API_URL = "https://api.stackexchange.com";

    public static void main(String... args) throws IOException {
        // Create REST adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of stackOverflow
        StackOverflowRestApi stackOverflow = retrofit.create(StackOverflowRestApi.class);

        boolean hasMore = true;
        int page = 1;

        //there is no sense to request users in multi threads, because StackExchange blocks ip address if there are to many requests from single ip address
        //this is why i use page size as mach as possible;
        while(hasMore)
        {
            // Create a call instance for getting users.
            Call<CommonWrapperObject<StackOverflowUser>> call = stackOverflow.usersPaged("reputation", 230,1000,"desc"
                    , "stackoverflow",10000, page);

            Response<CommonWrapperObject<StackOverflowUser>> execution = call.execute();

            if (!execution.isSuccessful())
            {

                System.out.println("Bad Request");
                System.out.println(execution.errorBody().string());

                break;
            }

            CommonWrapperObject<StackOverflowUser> commonWrapperObject = execution.body();

            commonWrapperObject.items.stream().parallel()
                    .filter(x-> x!= null && x.location!= null && x.location.contains("Moldova"))
                    .forEach(x->
                    {
                        if (x.display_name != null)
                            System.out.println(String.format("user %s, location %s", x.display_name,x.location));
                    }
            );
            hasMore = commonWrapperObject.has_more;
            page++;
        }

    }

}