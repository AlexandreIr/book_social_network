package tech.afsilva.book;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import tech.afsilva.book.role.Role;
import tech.afsilva.book.role.RoleRepository;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class BookNetApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookNetApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			roleRepository.findByName("USER")
					.ifPresentOrElse(role -> {}, () -> {
						roleRepository.save(
								Role.builder().name("USER").build()
						);
					});
		};
	}
}
