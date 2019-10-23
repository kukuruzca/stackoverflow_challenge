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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StackExchangeService {
    private static final String API_URL = "https://api.stackexchange.com";
    private static final String SITE = "stackoverflow";

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
                , SITE, 100, page, filterName, appKey);

        return executeCallAndGetResponse(call);
    }

    public List<String> getAllUserTags(int userId) throws IOException {
        List<String> userTags = new LinkedList<>();
        int page = 1;
        boolean hasMore = true;
        while (hasMore) {
            CommonWrapperObject<Tag> response = executeCallAndGetResponse(stackOverflowApi.getUserTags(userId, SITE, page, 100, appKey));
            userTags.addAll(response.items.stream().filter(x -> StringUtils.isNoneBlank(x.name)).map(x -> x.name).collect(Collectors.toList()));
            hasMore = response.has_more;
            page++;
        }
        return userTags;
    }

    public Filter createFilter(List<String> include, List<String> exclude, String base, boolean unsafe) throws IOException {
        CommonWrapperObject<Filter> response = executeCallAndGetResponse(stackOverflowApi.createFilter(include, exclude, base, unsafe, appKey));
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
