package vault.secrets.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import java.util.HashMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.support.Versioned;
import org.springframework.vault.core.VaultTemplate;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
		context.close();
	}

	@Override
	public void run(String... strings) throws Exception {
		VaultEndpoint vaultEndpoint = new VaultEndpoint();

		//Local running instance of Vault
		vaultEndpoint.setHost("127.0.0.1");
		vaultEndpoint.setPort(8200);
		vaultEndpoint.setScheme("http");

		// Authenticate with the token. This token should be valid.
		VaultTemplate vaultTemplate = new VaultTemplate(
				vaultEndpoint,
				new TokenAuthentication("hvs.Z8swD4DRYQDtuHGlkkxFkTRG"));

		// Writing the secret to Vault
		Map<String, String> data = new HashMap<>();
		data.put("password", "top_secret_password");

		vaultTemplate
				.opsForVersionedKeyValue("secret")
				.put("mysecretslocation/creds", data);

		System.out.println("Secret written successfully.");

		// Reading the secret from Vault
		Versioned<Map<String, Object>> secret = vaultTemplate
				.opsForVersionedKeyValue("secret")
				.get("mysecretslocation/creds");

		String password = "";
		if (secret != null && secret.hasData()) {
			password = (String) secret.getData().get("password");
		}

		if (!password.equals("top_secret_password")) {
			throw new Exception("Unexpected password");
		}

		System.out.println("Secret read successfully");
	}
}
