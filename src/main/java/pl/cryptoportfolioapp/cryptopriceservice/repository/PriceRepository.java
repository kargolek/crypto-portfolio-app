package pl.cryptoportfolioapp.cryptopriceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.cryptoportfolioapp.cryptopriceservice.model.Price;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

}
