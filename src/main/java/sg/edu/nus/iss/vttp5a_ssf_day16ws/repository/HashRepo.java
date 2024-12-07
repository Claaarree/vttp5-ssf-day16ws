package sg.edu.nus.iss.vttp5a_ssf_day16ws.repository;

import java.time.Duration;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import sg.edu.nus.iss.vttp5a_ssf_day16ws.constants.Constant;


@Repository
public class HashRepo {

    @Autowired
    @Qualifier(Constant.redisTemplate)
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    HashOperations<String, String, String> hashOps;

    @PostConstruct
    public void init(){
        hashOps = redisTemplate.opsForHash();
    }

    // either use PostConstruct or initialise in the constructor

    // Actually if i do this then it is also only one instance right? yes
    // @Autowired
    // public HashRepo(@Qualifier("String-String") RedisTemplate<String, String> redisTemplate) {
    //     this.redisTemplate = redisTemplate;
    //     this.hashOps = redisTemplate.opsForHash();  // Initialize HashOperations
    // }

    
    public void addToHash(String redisKey, String field, String value) {
        redisTemplate.opsForHash().putIfAbsent(redisKey, field, value);
    }

    public String getFieldValue(String redisKey, String field) {
        String value = hashOps.get(redisKey, field);
        return value;
    }
    
    public Set<String> getAllFields(String redisKey) {
        Set<String> fieldsKeySet = hashOps.keys(redisKey);
        return fieldsKeySet;
    }

    public void editFieldValue(String redisKey, String field, String newValue) {
        redisTemplate.opsForHash().put(redisKey, field, newValue);
    }

    public void deleteField(String redisKey, String field) {
        redisTemplate.opsForHash().delete(redisKey, field);
    }

    public Boolean isFieldExists(String redisKey, String field) {
        return redisTemplate.opsForHash().hasKey(redisKey, field);
    }

    public void expire(String redisKey, Long expireValue) {
        Duration expireDuration = Duration.ofSeconds(expireValue);
        redisTemplate.expire(redisKey, expireDuration);
    }
    
}
