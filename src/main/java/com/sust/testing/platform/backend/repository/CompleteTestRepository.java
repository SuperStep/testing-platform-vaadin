package com.sust.testing.platform.backend.repository;

import com.sust.testing.platform.backend.IVectorResult;
import com.sust.testing.platform.backend.entity.CompleteTestAnswers;
import com.sust.testing.platform.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompleteTestRepository extends JpaRepository<CompleteTestAnswers, Long> {

    @Query(value = "SELECT vp.id as vector, SUM(ca.value) as value, MAX(vp.description)\n" +
            "FROM complete_test_answers ca \n" +
            "left join vector_psychotype vp on ca.vector_id = vp.id\n" +
            "WHERE ca.user_id=?1 and ca.test_id=?2 group by vp.id", nativeQuery = true)
    List<IVectorResult> getStats(Long user_id, Long test_id);

//    @Query(value = "SELECT vp.\"name\" as vector, SUM(ca.value) as value\n" +
//            "FROM complete_test_answers AS ca \n" +
//            "left join vector_psychotype vp on ca.vector_id = vp.id\n" +
//            "group by vp.\"name\" ", nativeQuery = true)
//    List<IVectorResult> getStats(String username);

    @Modifying
    @Query(value = "DELETE from complete_test_answers WHERE question_id=?1 and user_id=?2",
        nativeQuery = true)
    void deleteTestQuestion(Long question_id, Long user_id);

}
