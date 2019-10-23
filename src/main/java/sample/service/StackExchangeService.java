package sample.service;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sample.service.stackoverflow.objects.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class StackExchangeService {
    private static final String API_URL = "https://api.stackexchange.com";
    private static final String SITE = "stackoverflow";
    private static final int DEFAULT_PAGE_SIZE = 100;

    private final StackOverflowRestApi stackOverflowApi;
    private final String appKey;

    public StackExchangeService(String appKey) {
        this.appKey = appKey;
        // Create REST adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of stackOverflow REST api
        stackOverflowApi = retrofit.create(StackOverflowRestApi.class);
    }

    public CommonWrapperObject<User> getUsers(int page, int reputationMinimum, String filterName) throws IOException {
        // Create a call instance for getting users.
        Call<CommonWrapperObject<User>> call = stackOverflowApi.getUsers(null, reputationMinimum, null, null
                , SITE, DEFAULT_PAGE_SIZE, page, filterName, appKey);

        return executeCallAndGetResponse(call);
    }

    public CommonWrapperObject<Tag> getAllUserTags(int userId, int page) throws IOException {
        return  executeCallAndGetResponse(stackOverflowApi.getUserTags(userId, SITE, page, DEFAULT_PAGE_SIZE, appKey));
    }

    public Filter createFilter(List<String> include, List<String> exclude, String base, boolean unsafe) throws IOException {
        CommonWrapperObject<Filter> response = executeCallAndGetResponse(stackOverflowApi.createFilter(include, exclude, base, unsafe, appKey));

        if(response.items!=null) {
            Optional<Filter> filterFound = response.items.stream().findAny();
            if (filterFound.isPresent())
                return filterFound.get();
        }
        throw new RuntimeException("Unable to create filter");
    }

    private <T> void handleResponse(Response<T> response) throws IOException {
        if (!response.isSuccessful()) {
            throw new RuntimeException(response.errorBody().string());
        }
    }

    private <T> T executeCallAndGetResponse(Call<T> call) throws IOException {
        Response<T> execution = call.execute();
        handleResponse(execution);
        return execution.body();
    }


}
