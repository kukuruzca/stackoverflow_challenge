package sample.service;


import sample.service.stackoverflow.objects.CommonWrapperObject;
import sample.service.stackoverflow.objects.Filter;
import sample.service.stackoverflow.objects.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public final class StackOverflowChallenge {
    public static void printUser(User soUser, List<String> userTags) {
        String userName = soUser.display_name != null ? soUser.display_name : "No name";
        String userLocation = soUser.location != null ? soUser.location : "No location";
        String linkToAvatar = soUser.profile_image != null ? soUser.profile_image : "No avatar";
        String linkToProfile = soUser.link != null ? soUser.link : "No link to profile";

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("User: ");
        stringBuffer.append(userName);
        stringBuffer.append(" | Location: ");
        stringBuffer.append(userLocation);
        stringBuffer.append(" | Answer count: ");
        stringBuffer.append(soUser.answer_count);
        stringBuffer.append(" | Question count: ");
        stringBuffer.append(soUser.question_count);
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

        Predicate<String> p1 = e -> e.equalsIgnoreCase("java");
        Predicate<String> p2 = e -> e.equalsIgnoreCase("c#");
        Predicate<String> p3 = e -> e.equalsIgnoreCase(".net");
        Predicate<String> p4 = e -> e.equalsIgnoreCase("docker");
        return tags.stream().anyMatch(p1.or(p2).or(p3).or(p4));
    }

    public static void main(String... args) throws IOException, InterruptedException {
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
                                    List<String> userTags = stackExchange.getAllUserTags(x.user_id);
                                    if (tagsFitsRequirements(userTags))
                                        printUser(x, userTags);
                                } catch (IOException e) {
                                    e.printStackTrace();
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