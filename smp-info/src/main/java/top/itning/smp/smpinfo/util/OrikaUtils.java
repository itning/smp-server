package top.itning.smp.smpinfo.util;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 实体映射工具类
 *
 * @author itning
 */
public class OrikaUtils {
    private static final MapperFactory MAPPER_FACTORY = new DefaultMapperFactory.Builder().build();

    /**
     * 将两个实体合并为一个DTO
     * input1，input2 不能全为 {@code null}
     *
     * @param input1 第一个输入实体
     * @param input2 第二个输入实体
     * @param result DTO类型
     * @param <R>    DTO
     * @param <A>    ENTITY
     * @param <B>    ENTITY
     * @return DTO类型
     */
    @NonNull
    public static <R, A, B> R doubleEntity2Dto(@Nullable A input1, @Nullable B input2, @NonNull Class<R> result) {
        if (input1 != null && input2 != null) {
            R r = MAPPER_FACTORY.getMapperFacade().map(input1, result);
            MAPPER_FACTORY.getMapperFacade().map(input2, r);
            return r;
        } else if (input1 != null) {
            return MAPPER_FACTORY.getMapperFacade().map(input1, result);
        } else {
            return MAPPER_FACTORY.getMapperFacade().map(input2, result);
        }
    }
}
