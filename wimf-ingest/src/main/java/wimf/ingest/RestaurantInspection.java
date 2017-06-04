package wimf.ingest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Immutable value representing a Restaurant Inspection result from New York open data
 */
final public class RestaurantInspection {

    @JsonCreator
    public static RestaurantInspection of(
            @JsonProperty("camis") final String businessID,
            @JsonProperty("dba") final String businessName,
            @JsonProperty("cuisine_description") final String cuisine,
            @JsonProperty("violation_code") final String violationCode,
            @JsonProperty("violation_description") final String violationDescription,
            @JsonProperty("grade") final String grade,
            @JsonProperty("boro") final String boro,
            @JsonProperty("building") final String building,
            @JsonProperty("street") final String street,
            @JsonProperty("zipcode") final String zipCode,
            @JsonProperty("phone") final String phone,
            @JsonProperty("score") final int score,
            @JsonProperty("inspection_date") final Date date,
            @JsonProperty("inspection_type") final String inspectionType) {

        final LocalDateTime inspectionDate =
                LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault());

        return new RestaurantInspection(
                businessID,
                businessName,
                cuisine,
                violationCode,
                violationDescription,
                grade,
                boro,
                building,
                street,
                zipCode,
                phone,
                score,
                inspectionDate,
                inspectionType);
    }

    public static RestaurantInspection of(
            final String businessID,
            final String businessName,
            final String cuisine,
            final String violationCode,
            final String violationDescription,
            final String grade,
            final String boro,
            final String building,
            final String street,
            final String zipCode,
            final String phone,
            final int score,
            final LocalDateTime inspectionDate,
            final String inspectionType) {

        return new RestaurantInspection(
                businessID,
                businessName,
                cuisine,
                violationCode,
                violationDescription,
                grade,
                boro,
                building,
                street,
                zipCode,
                phone,
                score,
                inspectionDate,
                inspectionType);
    }

    public final String businessID;
    public final String businessName;
    public final String cuisine;
    public final String violationCode;
    public final String violationDescription;
    public final String grade;
    public final String boro;
    public final String building;
    public final String street;
    public final String zipCode;
    public final String phone;
    public final int score;
    public final LocalDateTime inspectionDate;
    public final String inspectionType;

    private RestaurantInspection(
            final String businessID,
            final String businessName,
            final String cuisine,
            final String violationCode,
            final String violationDescription,
            final String grade,
            final String boro,
            final String building,
            final String street,
            final String zipCode,
            final String phone,
            final int score,
            final LocalDateTime inspectionDate,
            final String inspectionType) {

        this.businessID = businessID;
        this.businessName = businessName;
        this.cuisine = cuisine;
        this.violationCode = violationCode;
        this.violationDescription = violationDescription;
        this.grade = grade;
        this.boro = boro;
        this.building = building;
        this.street = street;
        this.zipCode = zipCode;
        this.phone = phone;
        this.score = score;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
    }
}
