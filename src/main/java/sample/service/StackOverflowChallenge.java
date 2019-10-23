package sample.service;


import org.apache.commons.lang3.StringUtils;
import sample.service.stackoverflow.objects.CommonWrapperObject;
import sample.service.stackoverflow.objects.Filter;
import sample.service.stackoverflow.objects.Tag;
import sample.service.stackoverflow.objects.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class StackOverflowChallenge {
    public static void printUser(User user, List<String> userTags) {
        String userName = user.display_name != null ? user.display_name : "No name";
        String userLocation = user.location != null ? user.location : "No location";
        String linkToAvatar = user.profile_image != null ? user.profile_image : "No avatar";
        String linkToProfile = user.link != null ? user.link : "No link to profile";

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("User: ");
        stringBuffer.append(userName);
        stringBuffer.append(" | Location: ");
        stringBuffer.append(userLocation);
        stringBuffer.append(" | Answer count: ");
        stringBuffer.append(user.answer_count);
        stringBuffer.append(" | Question count: ");
        stringBuffer.append(user.question_count);
        stringBuffer.append(" | Tags: ");
        stringBuffer.append(String.join(", ", userTags));
        stringBuffer.append(" | Link to profile: ");
        stringBuffer.append(linkToProfile);
        stringBuffer.append(" | Link to avatar: ");
        stringBuffer.append(linkToAvatar);
        stringBuffer.append("\n");
        System.out.println(stringBuffer.toString());
    }


    public static boolean tagsFitsRequirements(List<String> tags) {
        if (tags == null)
            throw new NullPointerException("tags is null");

        //have at least one of the desired tags
        Predicate<String> p1 = e -> e.equalsIgnoreCase("java");
        Predicate<String> p2 = e -> e.equalsIgnoreCase("c#");
        Predicate<String> p3 = e -> e.equalsIgnoreCase(".net");
        Predicate<String> p4 = e -> e.equalsIgnoreCase("docker");
        return tags.stream().anyMatch(p1.or(p2).or(p3).or(p4));
    }

    public static List<String> getAllUserTags(int userId,  StackExchangeService stackExchange) throws IOException {
        List<String> userTags = new LinkedList<>();
        int page = 1;
        boolean hasMore = true;
        while (hasMore) {
            CommonWrapperObject<Tag> response = stackExchange.getAllUserTags(userId, page);
            userTags.addAll(response.items.stream().filter(x -> StringUtils.isNoneBlank(x.name)).map(x -> x.name).collect(Collectors.toList()));
            hasMore = response.has_more;
            page++;
        }
        return userTags;
    }

    public static void main(String... args) throws IOException, InterruptedException {
        //AppKey retrieved for my app to enlarge number of quota requests
        StackExchangeService stackExchange = new StackExchangeService("TnWGwQfIf9SAk3Gkz2H5Lw((");
        //create new filter on base of default
        //add new fields in filter
        List<String> include = Arrays.asList("user.answer_count", "user.question_count");
        List<String> exclude = Arrays.asList();
        Filter filter = stackExchange.createFilter(include, exclude, "default", false);

        //start from page 1
        int page = 1;
        boolean hasMore = true;
        //there is no sense to request users in multi threads, because StackExchange blocks ip address if there are to many requests per second from single ip address
        while (hasMore) {
            CommonWrapperObject<User> commonWrapperObject = stackExchange.getUsers(page, 223, filter.filter);
            commonWrapperObject.items.stream()
                    //filter by answer count and location
                    .filter(x -> x.answer_count > 0)
                    .filter(x -> x != null && x.location != null && (x.location.contains("Moldova") || x.location.contains("Romania")))
                    .forEach(x ->
                            {
                                //final filter by tags
                                try {
                                    List<String> userTags = getAllUserTags(x.user_id, stackExchange);
                                    if (tagsFitsRequirements(userTags))
                                        printUser(x, userTags);
                                } catch (IOException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                    );
            hasMore = commonWrapperObject.has_more;
            page++;
            //throttling sleep
            Thread.sleep(500);
        }
    }

}