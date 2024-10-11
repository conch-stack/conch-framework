package com.nabob.conch.sample.sqlparser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * @author Adam
 * @since 2024/8/26
 */
public class SqlParserUtil {

    public static void main(String[] args) throws JSQLParserException {
//        String sql = "INSERT INTO `orderidseed` () VALUES ()";
        String sql = "UPDATE students " +
                "JOIN courses ON students.course_id = courses.course_id " +
                "JOIN grades ON students.student_id = grades.student_id " +
                "SET grades.grade = grades.grade + 5 " +
                "WHERE courses.course_name = 'Science'";

        // todo group by ; join; case when; having;

        Statement statement = CCJSqlParserUtil.parse(sql);
        System.out.println("Statement Name：" + statement.getClass().getName());
    }
}
