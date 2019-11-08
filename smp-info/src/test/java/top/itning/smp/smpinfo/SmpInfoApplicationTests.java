package top.itning.smp.smpinfo;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmpInfoApplicationTests {
   /* @Autowired
    private UserDao userDao;
    @Autowired
    private StudentUserDao studentUserDao;

    @Test
    void deleteAll() {
        userDao.findAll().forEach(user -> {
            userDao.delete(user);
            studentUserDao.deleteById(user.getId());
        });
    }

    @Test
    void contextLoads() {
        Role role = new Role();
        role.setId("1");
        Apartment apartment = new Apartment();
        apartment.setId("1");
        Random rand = new Random();
        for (int i = 0; i < 101; i++) {
            Map<String, String> infoMap = RandomValue.getInfoMap();
            User user = new User();
            user.setName(infoMap.get("name"));
            user.setTel(infoMap.get("tel"));
            user.setEmail(infoMap.get("email"));
            user.setUsername(UUIDs.get());
            user.setPassword(UUIDs.get());
            user.setRole(role);
            Date date = new Date();
            user.setGmtCreate(date);
            user.setGmtModified(date);
            User saved = userDao.save(user);

            StudentUser studentUser = new StudentUser();
            studentUser.setId(saved.getId());
            studentUser.setBirthday(new Date());
            studentUser.setSex(infoMap.get("sex").equals("男"));
            studentUser.setAge(rand.nextInt((30 - 20) + 1) + 20);
            studentUser.setStudentId((System.currentTimeMillis() + "").substring(5));
            studentUser.setIdCard(infoMap.get("idCard"));
            studentUser.setPoliticalStatus("共青团员");
            studentUser.setEthnic("汉");
            studentUser.setRoomNum((rand.nextInt((999 - 100) + 1) + 100) + "");
            studentUser.setApartment(apartment);
            studentUser.setAddress(infoMap.get("road"));
            studentUser.setBedNum((rand.nextInt((999 - 100) + 1) + 100) + "");
            studentUser.setGmtCreate(new Date());
            studentUser.setGmtModified(new Date());
            studentUserDao.save(studentUser);
        }
    }*/
}
