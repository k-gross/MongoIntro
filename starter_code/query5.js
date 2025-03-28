// Query 5
// Find the oldest friend for each user who has a friend. For simplicity,
// use only year of birth to determine age, if there is a tie, use the
// one with smallest user_id. You may find query 2 and query 3 helpful.
// You can create selections if you want. Do not modify users collection.
// Return a javascript object : key is the user_id and the value is the oldest_friend id.
// You should return something like this (order does not matter):
// {user1:userx1, user2:userx2, user3:userx3,...}

function oldest_friend(dbname) {
    db = db.getSiblingDB(dbname);

    let results = {};
    // TODO: implement oldest friends

    let flat_friends = db.users.aggregate([
        { 
            $unwind: "$friends"
        },
        {
            $project: {user_id: 1, friends: 1 }
        }
    ]).toArray();

    let map_of_friends = {};

    flat_friends.forEach((entry) => {
        if(!map_of_friends[entry.user_id]) {
            map_of_friends[entry.user_id] = [];
        }
        if(!map_of_friends[entry.friends]) {
            map_of_friends[entry.friends] = [];
        }

        map_of_friends[entry.user_id].push(entry.friends);
        map_of_friends[entry.friends].push(entry.user_id);
    });

    Object.keys(map_of_friends).forEach((user_id) => {
        let oldest = null;
        let oldest_age = Infinity;

        let user = db.users.findOne({ user_id: parseInt(user_id) });


        map_of_friends[user_id].forEach((friend_id) => {
            let curFriend = db.users.findOne({ user_id: friend_id });
            
            if(curFriend.YOB < oldest_age || (curFriend.YOB == oldest_age && curFriend.user_id < oldest.user_id)) {
                oldest_age = curFriend.YOB;
                oldest = curFriend;
            }
        });

        if (oldest) {
            results[parseInt(user_id)] = oldest.user_id;
        }
    });


    return results;
}
