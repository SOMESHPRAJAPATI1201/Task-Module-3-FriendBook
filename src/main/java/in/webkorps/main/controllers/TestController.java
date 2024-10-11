package in.webkorps.main.controllers;


import in.webkorps.main.dto.WrapperPosts;
import in.webkorps.main.entity.Following;
import in.webkorps.main.entity.Posts;
import in.webkorps.main.repository.FollowingsRepo;
import in.webkorps.main.repository.PostsRepo;
import in.webkorps.main.service.FollowingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private FollowingServices followingServices;

    @Autowired
    private PostsRepo postsRepo;

    @Autowired
    private FollowingsRepo followingsRepo;

}
