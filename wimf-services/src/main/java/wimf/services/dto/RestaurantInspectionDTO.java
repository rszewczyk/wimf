package wimf.services.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import wimf.domain.RestaurantInspection;

import java.time.LocalDateTime;
import java.util.List;

public final class RestaurantInspectionDTO {
    public static RestaurantInspectionDTO fromModel(final RestaurantInspection inspection) {
        return new RestaurantInspectionDTO(
                inspection.businessName,
                inspection.boro,
                inspection.grade,
                inspection.inspectionDate,
                inspection.businessID,
                inspection.cuisine,
                inspection.violationCode,
                inspection.score,
                inspection.inspectionType);
    }

    public static List<RestaurantInspectionDTO> fromModels(final List<RestaurantInspection> inspections) {
        return inspections.stream()
                .map(RestaurantInspectionDTO::fromModel)
                .collect(ImmutableList.toImmutableList());
    }

    public final String businessName;
    public final String boro;
    public final String grade;
    public final LocalDateTime inspectionDate;
    public final String businessID;
    public final String cuisine;
    public final String violationCode;
    public final int score;
    public final String inspectionType;

    @JsonCreator
    public static RestaurantInspectionDTO of(@JsonProperty("businessName") final String businessName,
                                             @JsonProperty("boro") final String boro,
                                             @JsonProperty("grade") final String grade,
                                             @JsonProperty("inspectionDate") final LocalDateTime inspectionDate,
                                             @JsonProperty("businessID") final String businessID,
                                             @JsonProperty("cuisine") final String cuisine,
                                             @JsonProperty("violationCode") final String violationCode,
                                             @JsonProperty("score") final int score,
                                             @JsonProperty("inspectionType") final String inspectionType) {
        return new RestaurantInspectionDTO(
                businessName,
                boro,
                grade,
                inspectionDate,
                businessID,
                cuisine,
                violationCode,
                score,
                inspectionType);
    }

    private RestaurantInspectionDTO(final String businessName,
                                    final String boro,
                                    final String grade,
                                    final LocalDateTime inspectionDate,
                                    final String businessID,
                                    final String cuisine,
                                    final String violationCode,
                                    final int score,
                                    final String inspectionType) {

        this.businessName = businessName;
        this.boro = boro;
        this.grade = grade;
        this.inspectionDate = inspectionDate;
        this.businessID = businessID;
        this.cuisine = cuisine;
        this.violationCode = violationCode;
        this.score = score;
        this.inspectionType = inspectionType;
    }
}
