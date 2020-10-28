import com.proj.dao.SysUserMapper;
import com.proj.entity.SysUser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SysUserMapperTest {
    @Autowired
    private SysUserMapper sysUserMapper;
    void querySysUser() {
        for (SysUser sysUser : sysUserMapper.querySysUser()) {
            System.out.println(sysUser);
        }
    }
}