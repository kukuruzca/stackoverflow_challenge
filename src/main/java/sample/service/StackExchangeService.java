package sample.service;

import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sample.service.stackoverflow.objects.CommonWrapperObject;
import sample.service.stackoverflow.objects.Filter;
import sample.service.stackoverflow.objects.Tag;
import sample.service.stackoverflow.objects.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StackExchangeService {
    private static final String API_URL = "https://api.stackexchange.com";
    private static final String SITE = "stackoverflow";

    private final StackOverflowRestApi stackOverflowApi;
    private final String appKey;
    private Filter customFilter;


    public StackExchangeService(String appKey) throws IOException {
        this.appKey = appKey;
        // Create REST adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of stackOverflow REST api
        stackOverflowApi = retrofit.create(StackOverflowRestApi.class);
    }

    public List<String> getAllUserTags(int userId) throws IOException {
        //TODO: add key
        List<String> userTags = new LinkedList<>();
        int page = 1;
        boolean hasMore = true;
        while (hasMore)
        {
            CommonWrapperObject<Tag> response = executeCallAndGetResponse(stackOverflowApi.getUserTags(userId, SITE, page, 100));
            userTags.addAll(response.items.stream().filter(x -> StringUtils.isNoneBlank(x.name)).map(x -> x.name).collect(Collectors.toList()));
            hasMore = response.has_more;
            page++;
        }
        return userTags;
    }

    public CommonWrapperObject<User> getUsers(int page, int reputationMinimum, Filter filter) throws IOException {
        // Create a call instance for getting users.
        Call<CommonWrapperObject<User>> call = stackOverflowApi.getUsers("reputation", reputationMinimum, null, "asc"
                , SITE, 100, page, filter.filter, appKey);

        return  executeCallAndGetResponse(call);
    }

    public Filter createFilter(List<String> include , List<String> exclude, String base, boolean unsafe) throws IOException {
        CommonWrapperObject<Filter> response = executeCallAndGetResponse(stackOverflowApi.createFilter(include, exclude, base, unsafe));
        Optional<Filter> filterFound = response.items.stream().findAny();
        if (filterFound.isPresent())
            return filterFound.get();
        else
            throw new RuntimeException("Unable to create filter");
    }

    private <T> void handleResponse(Response<T> response) throws IOException {
        if (!response.isSuccessful()) {
            //TODO:
            System.out.println("Bad Request");
            System.out.println(response.errorBody().string());
            throw new RuntimeException("Stop");
        }
    }

    private <T> T executeCallAndGetResponse(Call<T> call) throws IOException {
        Response<T> execution = call.execute();
        handleResponse(execution);
        return execution.body();
    }



}
