# axt-cache
Simple cache abstraction over ehcache.

Add the jitpack.io repository 

```xml
   
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
   
```

Add this project as a dependency

```xml
   
<dependency>
	<groupId>com.github.alextremp</groupId>
	<artifactId>axt-cache</artifactId>
	<version>1.0.0</version>
</dependency>
   
```


##Usage

###Example of code not using cache 

```java
   
@Service
public class SecurityUserDetailsService implements UserDetailsService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userMapper.getByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.addAll(userRoles2grantedAuthorities(user));
		return new UserAuth(user, grantedAuthorities);
	}

}
   
```


###Example of same code using cache

```java
   
@Service
public class SecurityUserDetailsService extends AbstractModelCache<UserAuth, StringModelCacheKeyImpl> implements UserDetailsService {

	@Autowired
	private UserMapper userMapper;

	public SecurityUserDetailsService() {
		super("SecurityUserDetails", 300L, 50);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return getEntry(new StringModelCacheKeyImpl(username));
	}

	@Override
	protected UserAuth load(StringModelCacheKeyImpl usernameKey) {
		User user = userMapper.getByUsername(usernameKey.getKey());
		if (user == null) {
			throw new UsernameNotFoundException(usernameKey.getKey());
		}
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.addAll(userRoles2grantedAuthorities(user));
		return new UserAuth(user, grantedAuthorities);
	}

}
   
```

