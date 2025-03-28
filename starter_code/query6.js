// Query 6
// Find the average friend count per user.
// Return a decimal value as the average user friend count of all users in the users collection.

function find_average_friendcount(dbname) {
    db = db.getSiblingDB(dbname);

    // TODO: calculate the average friend count

    let totalUsers = db.users.count();

    let friend_count = db.users.aggregate([
        {
            $project: {
                friend_count: { $size: "$friends" } 
            }
        },
        {
            $group: {
                _id: null, 
                total_friends: { $sum: "$friend_count" }, 
                total_users: { $sum: 1 } 
            }
        }
    ]).toArray();

    
    return friend_count[0].total_friends / totalUsers;
}
