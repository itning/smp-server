package top.itning.smp.smpsecurity.security;

import java.lang.annotation.*;

/**
 * @author itning
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MustLogin {
    /**
     * 登录角色
     *
     * @return 角色数组
     */
    ROLE[] role();

    enum ROLE {
        /**
         * 学生
         */
        STUDENT("1", "学生"),
        /**
         * 教师
         */
        TEACHER("2", "教师"),
        /**
         * 辅导员
         */
        COUNSELOR("3", "辅导员");

        private String id;
        private String name;

        ROLE(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "ROLE{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
