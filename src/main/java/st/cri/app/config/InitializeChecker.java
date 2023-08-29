package st.cri.app.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class InitializeChecker implements CommandLineRunner {

    private final Logger logger = LogManager.getLogger(InitializeChecker.class);
    private	final Environment environment;

    public InitializeChecker(final Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        logger.info("CHEQUEANDO PROPERTIES...");
		executeCheck("SWAGGER URL", "swagger.url");

        logger.info("PROPIEDADES OBLIGATORIAS CHECKEADAS");
        logger.info("------------------------------------");
    }

    private void executeCheck(final String name, final String property){

        try{
            String value = environment.getRequiredProperty(property);
            if (environment.acceptsProfiles(Profiles.of("!prod"))) {
                logger.info("{} : {}", name, value);
            }
        } catch (Exception e) {
            logger.error("NO INICIO LA APLICACION POR AUSENCIA DE PROPERTY: {}", name);
            throw new RuntimeException(e);
        }
    }
}
