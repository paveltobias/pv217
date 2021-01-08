package cz.fi.muni.pv217.dlp;

import cz.fi.muni.pv217.dlp.extern.PlagiarismDTO;
import cz.fi.muni.pv217.dlp.extern.SolutionDTO;
import io.smallrye.common.annotation.Blocking;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.reactivestreams.Publisher;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class PlagiarismResource {

    private static final Logger LOG = Logger.getLogger(PlagiarismResource.class);
    public List<String> checking = new ArrayList<String>(List.of("content", "Not stolen.", "I copied", "This is the original!"));



    @Inject
    @Channel("solutions")
    Publisher<SolutionDTO> solutionDTOPublisher;

    @Inject
    @Channel("plagiarism")
    Emitter<PlagiarismDTO> idEmitter;

    @Incoming("solutions")
    public void checkSolution(SolutionDTO solution) {
        LOG.info("Received solution dto:" + solution.toString());
        PlagiarismDTO plag = PlagiarismDTO.create(solution.id);
        if (solution.id != null && solution.content != null){
            for (String str : checking){
                if (str.equals(solution.content) ){
                    LOG.info("Solution with ID " + solution.id + "is not original.");

                    idEmitter.send(plag);
                }
            }
        }

    }
}
