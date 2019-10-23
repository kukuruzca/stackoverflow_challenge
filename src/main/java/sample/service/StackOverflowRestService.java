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
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public final class StackOverflowRestService {
    public static final String API_URL = "https://api.stackexchange.com";
    public static final String SITE = "stackoverflow";

    public static <T> void handleResponse(Response<T> response) throws IOException {
        if (!response.isSuccessful())
        {
            System.out.println("Bad Request");
            System.out.println(response.errorBody().string());
            throw new RuntimeException("Stop");
        }
    }

    public static <T> T executeCallAndGetResponse(Call<T> call) throws IOException {
        Response<T> execution = call.execute();
        handleResponse(execution);
        return execution.body();
    }


    public static Filter createFilter(StackOverflowRestApi api) throws IOException {

        List<String> include = Arrays.asList("user.answer_count", "user.question_count");
        List<String> exclude = Arrays.asList();

        CommonWrapperObject<Filter> response = executeCallAndGetResponse(api.createFilter(include, exclude, "default", false));
        Optional<Filter> filterFound = response.items.stream().findAny();
        if (filterFound.isPresent())
            return filterFound.get();
        else
            throw new RuntimeException("Unable to create filter");
    }

    public static List<String> getUserTags(int userId, StackOverflowRestApi api) throws IOException {
        CommonWrapperObject<Tag> response = executeCallAndGetResponse(api.getUserTags(userId, SITE));
        return  response.items.stream().filter(x-> StringUtils.isNoneBlank(x.name)).map(x->x.name).collect(Collectors.toList());
    }

    public static void printUser(User soUser, List<String> usertags)
    {
        String userName = soUser.display_name != null ? soUser.display_name : "No name";
        String userLocation = soUser.location != null ? soUser.location : "No location";
        String linkToAvatar = soUser.profile_image != null ? soUser.profile_image : "No avatar";
        String linkToProfile = soUser.link != null ? soUser.link : "No link to profile";

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("User: ");
        stringBuffer.append(userName);
        stringBuffer.append(" | Location: ");
        stringBuffer.append(userLocation);
        stringBuffer.append(" | Answer count: ");
        stringBuffer.append(soUser.answer_count);
        stringBuffer.append(" | Question count: ");
        stringBuffer.append(soUser.question_count);
        stringBuffer.append(" | Tags: ");
        stringBuffer.append(String.join(", ", usertags));
        stringBuffer.append(" | Link to profile: ");
        stringBuffer.append(linkToProfile);
        stringBuffer.append(" | Link to avatar: ");
        stringBuffer.append(linkToAvatar);
        stringBuffer.append("\n");
        System.out.println(stringBuffer.toString());
    }

    public static void main(String... args) throws IOException, InterruptedException {

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
            Call<CommonWrapperObject<User>> call = stackOverflowApi.getUsers("reputation", 223,null,"asc"
                    , SITE,100, page, customFilter.filter, "TnWGwQfIf9SAk3Gkz2H5Lw((");

            CommonWrapperObject<User> commonWrapperObject = executeCallAndGetResponse(call);
            commonWrapperObject.items.stream()
                    .filter(x -> x.answer_count>0)
                    .filter(x-> x!= null && x.location!= null && (x.location.contains("Moldova") || x.location.contains("Romania")))
                    .forEach(x->
                    {
                        try {
                            List<String> usertags = getUserTags(x.user_id, stackOverflowApi);
                            Predicate<String> p1 = e -> e.toLowerCase().equals("java");
                            Predicate<String> p2 = e -> e.toLowerCase().equals("c#");
                            Predicate<String> p3 = e -> e.toLowerCase().equals(".net");
                            Predicate<String> p4 = e -> e.toLowerCase().equals("docker");
                            boolean foundtag= usertags.stream().anyMatch(p1.or(p2).or(p3).or(p4));

                            if(foundtag)
                                printUser(x, usertags);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
            );
            hasMore = commonWrapperObject.has_more;
            page++;
            Thread.sleep(1000);
        }

    }

}