package pl.cryptoportfolioapp.cryptopriceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cryptoportfolioapp.cryptopriceservice.model.Cryptocurrency;

import java.util.List;
import java.util.Optional;

@Repository
public interface CryptocurrencyRepository extends JpaRepository<Cryptocurrency, Long> {
    @Query(value = "SELECT * FROM cryptocurrency WHERE name in :name", nativeQuery = true)
    List<Cryptocurrency> findByName(@Param("name") List<String> name);

}
