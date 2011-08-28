package eu.alertproject.iccs.stardom.analyzers.ml;

import eu.alertproject.iccs.stardom.analyzers.its.connector.DefaultItsHistoryAction;
import eu.alertproject.iccs.stardom.datastore.api.dao.ItsMlDao;
import eu.alertproject.iccs.stardom.domain.api.Identity;
import eu.alertproject.iccs.stardom.domain.api.Profile;
import eu.alertproject.iccs.stardom.domain.api.ml.ItsMl;
import eu.alertproject.iccs.stardom.identifier.api.Identifier;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: fotis
 * Date: 27/08/11
 * Time: 18:45
 */
@Service
public class ItsMlService {

    private Logger logger = LoggerFactory.getLogger(ItsMlService.class);


    @Autowired
    ItsMlDao itsMlDao;

    @Autowired
    Identifier identifier;

    @Transactional
    public ItsMl findOrCreate(Integer bugId){

        ItsMl lastestForBugId = itsMlDao.findLastestForBugId(bugId);
        if(lastestForBugId != null){
            return lastestForBugId;
        }

        ItsMl newBug = new ItsMl();

        return  itsMlDao.insert(newBug);

    }

    @Transactional
    public void recordItsHistory(Profile profile, DefaultItsHistoryAction action) {

        String what = action.getWhat();
        if(StringUtils.equalsIgnoreCase(what, "Status")){


            ItsMl itsMl = itsMlDao.findLastestForBugId(action.getBugId());

            ItsMl newBugAction = new ItsMl();

            if(itsMl != null){
                newBugAction.setUuid(itsMl.getUuid());
            }

            newBugAction.setBugId(action.getBugId());
            newBugAction.setStatus(action.getAdded());
            newBugAction.setWhen(action.getDate());
            ItsMl insert = itsMlDao.insert(newBugAction);
            logger.trace("void event() Status Change {} ",insert);

        }else if(StringUtils.endsWithIgnoreCase(what,"AssignedTo")){

            //get the identity
            Profile p = new Profile();
            p.setEmail(action.getAdded());
            Identity identity = identifier.find(p);


            //find the bug
            ItsMl byBugId = itsMlDao.findLastestForBugId(action.getBugId());
            ItsMl newBugAction = new ItsMl();

            if(byBugId !=null ){
                newBugAction.setStatus(byBugId.getStatus());
            }

            newBugAction.setUuid(identity.getUuid());
            newBugAction.setBugId(action.getBugId());
            newBugAction.setWhen(action.getDate());

            ItsMl insert = itsMlDao.insert(newBugAction);
            logger.trace("void event() Assigned to Changed {}",insert);

        }

    }
}
