package in.webkorps.main.repository;

import in.webkorps.main.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentsRepo extends JpaRepository<PostComments,Integer> {

    List<PostComments> findByPostPostId(Integer id);

}
