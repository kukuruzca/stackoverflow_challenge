package sample.service;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sample.service.stackoverflow.objects.CommonWrapperObject;
import sample.service.stackoverflow.objects.Filter;
import sample.service.stackoverflow.objects.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public final class StackOverflowRestService {
    public static final String API_URL = "https://api.stackexchange.com";

    public static <T> void handleResponse(Response<T> response) throws IOException {
        if (!response.isSuccessful())
        {
            System.out.println("Bad Request");
            System.out.println(response.errorBody().string());
            throw new RuntimeException("Stop");
        }
    }

    public static Filter createFilter(StackOverflowRestApi api) throws IOException {

        List<String> include = Arrays.asList("user.answer_count", "user.question_count");
        List<String> exclude = Arrays.asList();
        Response<CommonWrapperObject<Filter>> execution = api.createFilter(include, exclude, "default", false).execute();

        handleResponse(execution);
        Optional<Filter> filterFound = execution.body().items.stream().findAny();
        if (filterFound.isPresent())
            return filterFound.get();
        else
            throw new RuntimeException("Unable to create filter");
    }

    public static void main(String... args) throws IOException {
        // Create REST adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of stackOverflow REST api
        StackOverflowRestApi stackOverflowApi = retrofit.create(StackOverflowRestApi.class);

        Filter customFilter =  createFilter(stackOverflowApi);

        boolean hasMore = true;
        int page = 1;

        //there is no sense to request users in multi threads, because StackExchange blocks ip address if there are to many requests from single ip address
        //this is why i use page size as mach as possible (max 100);
        //maximum quota per day - 300 requests
        while(hasMore)
        {
            // Create a call instance for getting users.
            Call<CommonWrapperObject<User>> call = stackOverflowApi.usersPagedWithFilter("reputation", 230,1000,"desc"
                    , "stackoverflow",100, page, customFilter.filter);

            Response<CommonWrapperObject<User>> execution = call.execute();
            handleResponse(execution);
            CommonWrapperObject<User> commonWrapperObject = execution.body();

            commonWrapperObject.items.stream().parallel()
                    .filter(x-> x!= null && x.location!= null && (x.location.contains("Moldova") || x.location.contains("Romania")))
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