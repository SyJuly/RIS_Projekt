package gameLWJGL.objects;

import gameLWJGL.world.Camera;
import gameLWJGL.world.World;
import network.IMsgApplicator;
import network.networkMessages.DynamicObjectsMsg;

import java.io.IOException;
import java.util.*;


public class ObjectHandler implements IMsgApplicator<DynamicObjectsMsg>{

    private PlayerManager playerManager;
    private Map<String, GameObject> objects = new HashMap<>();
    private ArrayList<GameObject> updatedObjects = new ArrayList<>();
    private ArrayList<GameObject> removedObjects = new ArrayList<>();
    private List<IObjectHolder> objectHolders = new ArrayList<>();

    public ObjectHandler(PlayerManager playerManager, World world){
        this.playerManager = playerManager;
        objectHolders.add(playerManager);
        objectHolders.add(world);
    }

    public void updateObjects(){
        for (GameObject gameObject : objects.values()) {
            if(gameObject.update()){
                updatedObjects.add(gameObject);
            }
        }
    }

    public void render(Camera camera){
        for (GameObject gameObject : objects.values()) {
            gameObject.render(camera);
        }
    }

    public void updateObjectsList(){
        Iterator<IObjectHolder> iter = objectHolders.iterator();
        while(iter.hasNext()) {
            IObjectHolder objectHolder = iter.next();
            GameObject[] newlyCreatedObjects = objectHolder.getNewlyCreatedObjects();
            String[] removedObjectIds = objectHolder.getRemovedObjects();
            for(int i = 0; i < newlyCreatedObjects.length; i++){
                GameObject gameObject = newlyCreatedObjects[i];
                objects.put(gameObject.id, gameObject);
            }
            for(int i = 0; i < removedObjectIds.length; i++){
                String removedObjectId = removedObjectIds[i];
                GameObject removedGameObject = objects.remove(removedObjectId);
                removedObjects.add(removedGameObject);
            }
        }
    }

    public List<GameObject> getDynamicObjects(){
        return new ArrayList<>(objects.values());
    }

    public void createOrUpdateObject(float x, float y, float width, float height, String id, int objectTypeCode, Float[] specifics){
        if(!objects.containsKey(id)){
            ObjectType type = ObjectType.values()[objectTypeCode];
            switch (type) {
                case PLAYER:
                    playerManager.createPlayer(x,y,id, specifics); break;
                default: return;
            }
        } else {
            GameObject gameObject = objects.get(id);
            gameObject.x = x;
            gameObject.y = y;
            gameObject.width = width;
            gameObject.height = height;
        }
    }

    public void acknowledgeEndOfDynamicObjectsMsg(){
        updateObjectsList();
    }

    public boolean shouldSendMessage() {
        return (updatedObjects.size() > 0 || removedObjects.size() > 0);
    }

    @Override
    public DynamicObjectsMsg getMessage() {
        return new DynamicObjectsMsg(this);
    }

    @Override
    public void receive(DynamicObjectsMsg networkMsg) {
        try {
            networkMsg.deserializeAndApplyData(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeObject(String removedObjectId) {
        Iterator<IObjectHolder> iter = objectHolders.iterator();
        while(iter.hasNext()) {
            IObjectHolder objectHolder = iter.next();
            objectHolder.removeObject(removedObjectId);
        }
        objects.remove(removedObjectId);
    }

    public List<GameObject> getUpdatedObjects() {
        return updatedObjects;
    }

    public List<GameObject> getRemovedObjects() {
        return removedObjects;
    }

    public void clearMsgLists() {
        updatedObjects.clear();
        removedObjects.clear();
    }
}