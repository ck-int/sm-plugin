import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class ReleaseTaskEntity {
    @NotBlank
    private String taskCode;
    @NotBlank
    private String cnStationNo;
    @NotBlank
    private String gpStationNo;

    private Long beginTime;

    private Long endTime;

    private Integer frequency;

}
