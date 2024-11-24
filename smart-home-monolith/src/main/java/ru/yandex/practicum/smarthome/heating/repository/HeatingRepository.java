package ru.yandex.practicum.smarthome.heating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthome.heating.entity.Heating;

@Repository
public interface HeatingRepository extends JpaRepository<Heating, Long> {
}
