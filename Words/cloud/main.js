var VERSION = .1;


Parse.Cloud.beforeSave('Game', function(request, response) {
  if (request.object.get('Version') >= VERSION) {
    response.success();
  } else {
    response.error("A new version is available. You must update to continue playing.");
  }
});
