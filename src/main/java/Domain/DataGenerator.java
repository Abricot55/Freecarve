package Domain;

import Common.DTO.VertexDTO;
import java.util.List;

/**
 * The {@code FileManager} class regroup functions which interact with files on the user's personal computer
 *
 * @author Adam Côté
 * @version 1.0
 * @since 2024-10-20
 */
class DataGenerator {

    /**
     * Converts a {@code ProjectState} into a series of GCode instructions. These instructions can later be saved as a file.
     *
     * @param state The {@code ProjectState} which needs to be converted into GCode
     * @return The {@code String equivalent of the GCode}
     */
    static String convertToGCode(ProjectState state) {
        List<Cut> cuts = state.getPanel().getCutList();
        StringBuilder instructions = new StringBuilder();

        //definition of constants
        String rotationSpeed = "S12000"; // rotation speed
        String movementSpeed = "F500"; // movement speed
        final String lineEnd = ";\n";

        //initiate CNC State
        instructions.append("G21" + lineEnd); //mm
        instructions.append("G17" + lineEnd); //xy plane
        instructions.append("G28" + lineEnd); //go to (0,0)

        List<Cut> cutlist = state.getPanel().getCutList();
        for (Cut cut : cutlist) {
            instructions.append("T").append(cut.getBitIndex() + 1).append(" M06").append(lineEnd); //select the tool
            for (VertexDTO vertex : cut.getPoints()) {
                if (vertex == cut.getPoints().get(0)) {
                    instructions.append("G00 X").append(vertex.getX()).append(" Y").append(vertex.getY()).append(lineEnd); //go to position of first point
                    instructions.append("M03 " + rotationSpeed + lineEnd); //starts the rotation of the tool
                }
                instructions.append("G09 X").append(vertex.getX()).append(" Y").append(vertex.getY()).append(" Z").append(vertex.getZ()).append(lineEnd); //cut to the point location
            }
            instructions.append("M05" + lineEnd); //stop the tool
            instructions.append("G00 Z0" + lineEnd); //go to the predefined Z safe spot
        }
        //end program
        instructions.append("M05" + lineEnd); // stops the bit rotation
        instructions.append("G28" + lineEnd); // return to (0,0)
        instructions.append("M02" + lineEnd); //end of the program

        return instructions.toString();
    }
}
