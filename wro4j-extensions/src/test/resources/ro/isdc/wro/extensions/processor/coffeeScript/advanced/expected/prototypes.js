(function() {

  String.prototype.dasherize = function() {
    return this.replace(/_/g, "-");
  };

}).call(this);