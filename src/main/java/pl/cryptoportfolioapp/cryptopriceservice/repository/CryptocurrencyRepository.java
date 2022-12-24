package pl.cryptoportfolioapp.cryptopriceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;

import java.util.Optional;

@Repository
public interface CryptocurrencyRepository extends JpaRepository<Cryptocurrency, Long> {
    @Query(value = "SELECT * FROM cryptocurrency WHERE name = :name", nativeQuery = true)
    Optional<Cryptocurrency> findByName(@Param("name") String name);

}
