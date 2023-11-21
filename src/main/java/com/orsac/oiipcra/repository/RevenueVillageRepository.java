package com.orsac.oiipcra.repository;

import com.orsac.oiipcra.entities.RevenueVillageBoundary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RevenueVillageRepository extends JpaRepository<RevenueVillageBoundary, Integer> {

    @Query(value ="SELECT revenue_village_boundary.gid, revenue_village_boundary.revenue_village_name,revenue_village_boundary.village_id," +
            "revenue_village_boundary.revenue_village_code, revenue_village_boundary.grampanchayat_name, revenue_village_boundary.grampanchayat_code," +
            "revenue_village_boundary.block_name, revenue_village_boundary.block_code," +
            "revenue_village_boundary.district_name, revenue_village_boundary.district_code," +
            "revenue_village_boundary.state_code, revenue_village_boundary.state_name,revenue_village_boundary.gp_id FROM oiipcra_oltp.village_boundary as revenue_village_boundary where " +
            " public.st_intersects(public.st_setsrid(public.st_makepoint(:longitude,:latitude),4326),revenue_village_boundary.geom)", nativeQuery = true)
    List<RevenueVillageBoundary> getVillageByIdByLngLat(double longitude, double latitude);


    @Query(value ="SELECT village.gid, village.revenue_village_name,village.village_id," +
            " village.revenue_village_code, village.grampanchayat_name, village.grampanchayat_code,village.block_name, village.block_code," +
            " village.district_name, village.district_code,village.state_code, village.state_name,village.gp_id,village.dist_id,village.block_id FROM oiipcra_oltp.village_boundary as village where " +
            " public.st_intersects(public.st_setsrid(public.st_geomfromtext(:geometry),4326),village.geom)", nativeQuery = true)
    List<RevenueVillageBoundary> getVillageByIdByLngLat(String geometry);




}
