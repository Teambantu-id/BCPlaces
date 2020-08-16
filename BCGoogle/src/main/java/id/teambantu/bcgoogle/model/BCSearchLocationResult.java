package id.teambantu.bcgoogle.model;

public class BCSearchLocationResult {
    private String formatted_address;
    private Geometry geometry;
    private String name;
    private String icon;

    public BCSearchLocationResult() {
    }

    public BCSearchLocationResult(String formatted_address, Geometry geometry, String name, String icon) {
        this.formatted_address = formatted_address;
        this.geometry = geometry;
        this.name = name;
        this.icon = icon;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public static class Geometry {
        private Location location;

        public Geometry() {
        }

        public Geometry(Location location) {
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public static class Location {
            private double lat;
            private double lng;

            public Location() {
            }

            public Location(double lat, double lng) {
                this.lat = lat;
                this.lng = lng;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }
        }
    }

}
