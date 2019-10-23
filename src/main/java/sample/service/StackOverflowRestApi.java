package sample.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sample.service.stackoverflow.objects.CommonWrapperObject;
import sample.service.stackoverflow.objects.Filter;
import sample.service.stackoverflow.objects.Tag;
import sample.service.stackoverflow.objects.User;

import java.util.List;

public interface StackOverflowRestApi {

    @GET("2.2/users")
    Call<CommonWrapperObject<User>> getUsers(
            @Query("sort") String sort,
            @Query("min") Integer min,
            @Query("max") Integer max,
            @Query("order") String order,
            @Query("site") String site,
            @Query("pagesize") Integer pagesize,
            @Query("page") Integer page,
            @Query("filter") String filter,
            @Query("key") String key
    );

    @GET("2.2/users/{ids}")
    Call<CommonWrapperObject<User>> getUsersById(
            @Path("ids")
            @Query("sort") String sort,
            @Query("order") String order,
            @Query("site") String site
    );

    @GET("2.2/filters/create")
    Call<CommonWrapperObject<Filter>> createFilter(
            @Query("include") List<String> include,
            @Query("exclude") List<String> exclude,
            @Query("base") String base,
            @Query("unsafe") boolean unsafe,
            @Query("key") String key

    );

    @GET("2.2/filters/{filters}")
    Call<CommonWrapperObject<Filter>> getFilters(
            @Path("filters") String filters);

    @GET("2.2/users/{ids}/tags")
    Call<CommonWrapperObject<Tag>> getUserTags(
            @Path("ids") Integer ids,
            @Query("site") String site,
            @Query("page") Integer page,
            @Query("pagesize") Integer pagesize,
            @Query("key") String key
    );
}

