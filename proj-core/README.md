Youth框架简介
=======

本文将会详细讲解 Youth框架基本结构和功能。

**注：本文仍在编写当中，其中部分内容非常陈旧，以后会逐步更新和完善。**

[[toc]]

# 项目介绍

Youth框架是为了交付团队java web项目开发，统一技术路线，提高开发效率而设计的一套公共基础框架。框架使用gradle构建工具构建，基于springboot2.0开发，集成了Mybatisplus持久层，Shiro安全控制，Druid数据库连接池，oracle及mysql驱动，swagger接口工具，springboot-junit单元测试，spring-cloud-config客户端，oauth2统一登录等一些其他功能。框架分为toolkit，framework，system，system-ui，springboot-starter及boot项目几个模块。使用人员可以直接通过maven仓库引入jar依赖使用，使用框架时可以根据此文档指导使用，如果还有疑问，可以直接联系框架开发人员。

项目文档地址：
[svn地址](svn://192.168.2.168/newsvnrepos/youth/java-web)
源代码路径：
[gitlab地址](http://192.168.2.97:18888/youth/java-web.git)
nexus私服仓库地址：
[nexus私服](http://192.168.2.97:18882)

## 模块介绍

* web-framework：java-web框架基础服务模块，主要提供了数据库连接，统一异常处理，BaseSevice与实体类，shiro安全控制，spring常用工具封装，全局配置获取以及一些其他扩展功能。使用springboot-starter和此项目可以迅速搭建一个包含以上功能的springboot项目。
* web-system：后台系统模块。提供了用户、人员、组织、RBAC权限、配置、字典等业务领域层服务。为应用层体用系统级服务。
* web-system-ui：后台管理应用模块，基于通用java后台项目提供的web接口服务，供前端调用。
* web-springboot-starter：集成springboot项目的自动配置相关功能，可以通过properties配置文件选择需要使用的框架提供的功能。主要包括过滤器配置，跨域CORS配置，传输加密，swagger接口工具，多租户功能，多数据源功能等等。该模块提供了一套缺省的java-web项目配置。boot项目只需要引入此jar包，即可快速搭建一个youth框架的springboot项目。
* toolkit：基础工具包，具体内容请查看javadoc文档。
* web-demo-boot：springboot demo启动项目。

## 基础功能介绍

### 基础dao层实体类和BaseService提供（附）
youth框架根据通用规范提供了基础的dao层实体类和实现通用数据库增删改查及批量操作的BaseService接口及实现类。

### shiro安全控制（附）
youth默认集成了shiro作为安全控制框架，使用者只需要引入框架boot-starter项目即可以集成进项目，如需自定义某些功能，可以通过注册springbean的方式提供自定义实现。例如demo项目自带注册过滤器配置：

```
@Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, YouthProperties youthProperties) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("auth", userUnauthorizedFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/page/login");
        shiroFilterFactoryBean.setSuccessUrl("/redirect/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/page/error403");
        Map<String,String> map = new LinkedHashMap<>();
        //登录
        map.put("/login","anon");
        //对外提供接口
        map.put("/api/**","anon");
        //公共页面请求
        map.put("/page/**", "anon");
        //错误页面
        map.put("/error*","anon");
        //开放页面或接口
        map.put("/open/**","anon");
        //静态资源
        map.put("/**/*.html","anon");
        map.put("/**/static/**","anon");
        //微信服务地址配置验证文件路径
        map.put("/*.txt", "anon");
        //swagger接口权限 开放
        if(youthProperties != null && youthProperties.getDebug().getSwagger().isEnabled()) {
            map.put("/swagger-ui.html", "anon");
            map.put("/webjars/**", "anon");
            map.put("/v2/**", "anon");
            map.put("/swagger-resources/**", "anon");
        }

        //所有接口用户认证
        map.put("/**", "auth");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }
```
框架默认提供了基于用户名密码的登录方式，使用者可通过添加新的realm实现类拓展其他登录方式，只需要继承自shiro的Realm接口，注册为springbean即可。

框架提供了ajax登录请求过滤器，可以过滤未登录状态下的ajax请求。

如需自定义操作session，可以使用ShiroUtil接口提供的getSessionManager接口获取session管理类。

框架通用的身份信息使用UserProfile类表示，具体内容请查看javadoc文档。通过UserUtil工具类提供的接口可以获取当前登录用户信息。

如有其它不明白功能，可以直接向开发人员询问。

### 统一异常控制

框架默认提供了基于spring controller增强的统一异常控制功能，GlobalExceptionHandler分为Exception，RuntimeException，SystemRuntimeException三个层级进行拦截，分别进行不同处理。

SystemRuntimeException异常是框架封装的自定义系统异常，定义为普通系统运行时异常，通常普通系统错误都可以通过抛出此异常来控制。可以通过定义SystemError错误类型来初始化该异常。
RuntimeException异常是jre提供的运行时异常，捕获到此异常时，框架会统一返回前端系统错误的提示内容。
Exception异常是所有异常的基类，任何未被捕获的异常都会在此被处理，捕获到此异常时，系统统一返回跳转错误页面。

使用者可以根据需求通过拓展异常类型，达到同样的功能。

### 数据库连接池

框架目前只支持阿里的druid数据库连接池，demo项目中配置了oracle、mysql俩套默认的连接池参数配置，单个数据源情况下使用者可以通过直接修改配置文件数据库连接参数配置数据源。

### Spring数据库事务管理

框架集成了Spring的数据库事务管理功能，使用可以通过声明式方式[code @Transactional]使用基于session的事务控制。

### MybatisPlus持久层

框架集成了MybatisPlus2.0框架，并且默认集成了分页插件[code PaginationInterceptor]和性能分析插件[code PerformanceInterceptor]，其中性能分析插件的生效范围是spring prifile为dev和test的环境。

基于MybatisPlus框架，youth框架提供了[code SysBaseEntity]、[code SysBaseService]和 [code BaseServiceImpl]基础封装。其中[code SysBaseEntity]提供了id、createtime、modifytime的基础字段，基础service提供业务对象基本的增删改查和批量操作的接口，使用可以通过继承方式获取改接口功能。

### 其他功能

此外框架还提供了诸如spring 上下文工具类，spring环境工具类、获取spring公共配置、常用前端返回对象封装[code PageRequestBody][code ReqBody][code ResBody]等。

## 拓展功能

### 多数据源

框架集成了多数据源的功能，使用是需要在配置文件中配置开启多数据源功能，并且设置默认数据源：

```
# 开启多数据源功能
youth.db.multiple-data-source.enabled=false
youth.db.multiple-data-source.default-data-source=dataSource
```
然后通过注册springbean方式注册数据源，可以使用druid连接池配置方式（注解内容为配置文件参数前缀）：

```
    @Bean("datasource1")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druid(){
        return  new DruidDataSource();
    }
```
框架会默认搜集所有已注册数据源，统一管理。
由于实现数据源切换是通过springaop方式实现，所以使用时切换数据源可以通过在springbean接口方法加上@DataSource注解，例如：

```
    @Override
    @DataSource("datasource1")
    public List<ConfigEntity> find(ConfigEntity params)
```
框架会自动根据注解配置切换数据源。

### 统一事件机制（附）

框架集成了谷歌guava EventBus事件总线机制。使用者通过注册监听方法来监听需要监听的事件，例如：

```
@Component
public class UserListner {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserListner.class);

    @Autowired
    PersonnelService personnelService;


    @Subscribe
    public void conTask(PersonnelEntityBatchEventImpl event) {
        if (event.getType().equals(EventType.ENTITY_INSERT_BATCH_AFTER)) {
            Collection<PersonnelEntity> entitys = event.getEntitys();

        }
    }

    @Subscribe
    public void conTask(PersonnelEntityEventImpl event) {
        if (event.getType().equals(EventType.ENTITY_INSERT_AFTER)) {
            PersonnelEntity entity = event.getEntity();
            PersonnelEntity p = personnelService.findById(entity.getId());
            if (p != null) {
                System.out.println("------------------------查出来了------------------------------------");
                System.out.println("------------------------实体id:" + p.getId());
            }
        }
    }

    @Subscribe
    public void conTask2(PersonnelEntityEventImpl event) {
        if (event.getType().equals(EventType.ENTITY_UPDATE_BATCH_BEFORE)) {
            PersonnelEntity entitys = event.getEntity();

        }
    }

    @Subscribe
    public void conTask2(PersonnelEntityBatchEventImpl event) {
        if (event.getType().equals(EventType.ENTITY_UPDATE_BATCH_BEFORE)) {
            Collection<PersonnelEntity> entitys = event.getEntitys();

        }
    }

}
```
目前支持的事件类型有用户、人员、组织单位以及基础entity动作事件，包括创建、删除、修改。

### 缓存

框架集成了谷歌guava工具包轻量级Cache功能，提供了LinkedCache、LoadingCache、LocalCache三种缓存类型。
* LinkedCache是有序缓存，可以缓存有序数据。
* LoadingCache是热加载缓存，可以自动控制缓存数据的更新时间与失效时间，并自动获取最新数据加入缓存。
* LocalCache是本地缓存，提供基本的缓存功能，需要自己管理缓存数据。
使用者可以通过CacheBuilder提供的缓存创建接口创建自己需要的缓存类型，所有已创建的缓存内容都可以通过CacheManager重新通过名称获取并操作，例如：

```
@PostConstruct
    public void init() throws Exception{
        ScopeContext.off();
        try {
            MenuServiceImpl menuServiceImpl = this;
            cache = (LocalCache) CacheBuilder.newBuilder()
                    .setConcurrencyLevel(10)
                    .setCacheLoader(new CacheLoader<String,MenuEntity>() {
                        @Override
                        public MenuEntity load(String id) throws Exception {
                            return menuServiceImpl.findById(id);
                        }

                        @Override
                        public Map<String, MenuEntity> loadAll() throws Exception {
                            return menuServiceImpl.findAll().stream().collect(Collectors.toMap(MenuEntity::getId, menu -> menu));
                        }
                    })
                    .build(CacheImplementorEnum.GUAVA, CacheTypeEnum.LOCAL, "menuCache");
            cache.refresh();
        } catch (Exception e) {
            LOGGER.error("init menu cache error:{}", e.getMessage(), e);
        } finally {
            ScopeContext.clear();
        }
    }
```
### CORS跨域控制

框架集成了CORS跨域控制功能，使用者可以通过配置文件直接配置使用：

```
# cors配置
youth.security.cors.enabled=true
youth.security.cors.allowedOrigin=*
youth.security.cors.allowedHeader=*
youth.security.cors.allowedMethod=*
youth.security.cors.path=/**
```
### 传输加密

框架提供了传输加密控制，可以通过配置文件开启，目前提供了RSA+AES的加密方式：

```
## 前后台传输加密
youth.security.cipher.enabled=true
youth.security.cipher.strategy=AR
```
注：前端也需要开启加密传输

### swagger接口工具（附）

框架提供了swagger接口工具功能，默认扫描全部包，可以通过配置文件开启功能：

```
# 开启swagger-ui功能
youth.debug.swagger.enabled=true
```
### 多租户

框架提供了基于mybatis 拦截器级别的sql多租户功能。数据新增时，根据当前登录用户确定新增数据的所属。使用时候需要首先通过配置文件开启多租户功能：

```
# 开启scope字段数据权限控制
youth.scope.enabled=false
```
然后需要实现多租户的功能业务实体类需要继承自ScopeEntity类，增加scope字段。
编码时框架提供了多种方式控制本次查询是否使用多租户功能，如可以通过在springbean类或方法上加上DataScope注解控制是否开启多租户，也可以手动在代码中直接通过ScopeHelper工具类开启和关闭多租户查询。
注：手动开启功能时，一定不要忘记在查询结束时关闭多租户功能。

### 线程池（附）

框架默认集成了spring线程池功能和定时调度功能，使用请阅读官网说明。TaskExecutor TaskScheduler

### 业务对象逻辑删除（附）

框架提供了用户、人员、组织单位业务对象的逻辑删除功能使用请阅读详细说明：
[逻辑删除]("docs/LogicDelete.md")

### 审计日志


## 架构功能

### 配置中心

框架集成了spring-cloud-config配置中心功能，使用时需要在boot项目中加入配置文件bootstrap.properties：

```
# 启用配置中心
spring.cloud.config.enabled=true
# 配置profile
spring.cloud.config.profile=${spring.profiles.active}
# 配置label
spring.cloud.config.label=master
# 配置中心地址
spring.cloud.config.uri=http://192.168.2.155:18888
# 拉取配置失败抛异常
spring.cloud.config.fail-fast=true
```
可以直接配置springboot参数或通过Global工具类在代码获取公共配置。具体使用方法请参考配置中心说明文档。


## 系统介绍

### 系统通用API（附）

框架系统模块提供了系统运行的一些基础核心的AIP, 包括获取系统缓存中的数据接口, 通用工具接口, 系统登录接口, 获取当前登录用户的人员组织机构信息和角色权限信息。

### 基于RBAC模型的权限控制模块（附）

RBAC是一种的权限控制模型，其中的核心元素为：用户，角色，资源，操作，权限。
该模型通过：用户关联角色，角色关联权限，操作关联权限与资源，从而实现对用户的权限控制。
为方便对大量用户的授权操作，提供了用户组来管理用户，可以通过为用户组授权从而代替为该用户组中每个用户授权的操作。
同时该模块还提供了授权，获取权限，验证权限的接口。


### 系统基础功能（附）

系统基础功能主要是为了满足各项目的公共基础功能，框架提供的系统基础功能主要包括组织机构，人员，配置，字典，菜单资源，接口资源，页面元素资源等管理功能。

* * *
返回 [目录](welcome)
