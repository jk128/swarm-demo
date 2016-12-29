package ch.maxant.demo.swarm.framework.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;

/** Verifies Json Web Tokens by checking their signature is OK for the given public key. */
public class Verifier {

    private String publicKey;

    public Verifier(String publicKey){
        this.publicKey = publicKey;
    }

    /**
     * @throws ExpiredJwtException
     * @throws SignatureException
     */
    public Claims verify(String token) {
        //http://stackoverflow.com/questions/28294663/how-to-convert-from-string-to-publickey
        byte[] publicBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Claims body = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(token).getBody();

            //System.out.println(body.getSubject()); //b1c112ff-9143-4d94-af78-7dc8511fa425 => user ID
            //System.out.println(body.getAudience()); //googledemo => Client name
            //System.out.println(body.getIssuer()); //https://auth.maxant.ch/auth/realms/tullia

            return body;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param pathToRoles e.g. resource_access.applicationName.roles
     * @return The roles
     * @throws ExpiredJwtException
     * @throws SignatureException
     */
    public Collection<String> verifyAndGetRoles(String token, String pathToRoles) {
        Claims body = verify(token);

        String[] paths = pathToRoles.split("\\.");
        Map map = (Map) body.get(paths[0]);
        for(int i = 1; i < paths.length; i++){
            map = (Map)map.get(paths[i]);
        }
        Collection<String> roles = (Collection<String>) map;
        return roles;
    }
}
